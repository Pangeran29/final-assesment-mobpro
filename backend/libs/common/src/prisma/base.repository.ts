import { Prisma, PrismaClient } from '@prisma/client';
import { RecordNotDeletedException, RecordNotUpdatedException } from '../exception/prisma';
import { RecordNotCreatedException } from '../exception/prisma/record-not-created.exception';
import { FindManyDto } from './dto/find-many.dto';
import { FindUniqueDto } from './dto/find-unique.dto';

export abstract class BaseRepository<T> {
  protected prisma: PrismaClient;
  protected model: Prisma.ModelName;

  constructor(model: Prisma.ModelName) {
    this.prisma = new PrismaClient();
    this.model = model;
  }

  async create(data: Partial<T>): Promise<T> {
    try {
      return await this.prisma[this.model].create({ data });
    } catch (error) {
      throw new RecordNotCreatedException(error);
    }
  }

  async update(id: number, data: Partial<T>): Promise<T> {
    try {
      return await this.prisma[this.model].update({
        where: { id },
        data,
      });
    } catch (error) {
      throw new RecordNotUpdatedException(error);
    }
  }

  async deleteById(id: number): Promise<T> {
    try {
      return await this.prisma[this.model].delete({
        where: { id },
      });
    } catch (error) {
      throw new RecordNotDeletedException(error)
    }
  }

  async findUnique({
    uniqueField,
    uniqueFieldValue,
    include,
  }: FindUniqueDto): Promise<T | null> {
    return await this.prisma[this.model].findUnique({
      where: { [uniqueField]: uniqueFieldValue },
      include,
    });
  }

  async findById(id: number): Promise<T | null> {
    return await this.prisma[this.model].findUnique({
      where: { id }
    });
  }

  async count(): Promise<number> {
    return await this.prisma[this.model].count();
  }

  async findMany({
    baseQueryFindManyDto,
    include,
    where,
  }: FindManyDto): Promise<T[] | any> {
    const { skip, take, sort } = baseQueryFindManyDto;

    const data = await this.prisma[this.model].findMany({
      take,
      skip,
      where,
      include,
      orderBy: {
        createdAt: sort,
      },
    });

    const totalData = await this.prisma[this.model].count({
      where,
    });

    return { totalData, [this.model]: data };
  }

  async executeRawQuery(query: string) {
    return await this.prisma.$executeRaw(Prisma.raw(`${query}`));
  }
}
