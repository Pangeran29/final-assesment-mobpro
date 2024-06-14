import { ApiProperty } from '@nestjs/swagger';
import { IsEnum, IsNumber, IsOptional } from 'class-validator';

export enum SortEnum {
  ASC = 'asc',
  DSC = 'desc',
}

export class BaseQueryFindManyDto {
  @ApiProperty({
    description: 'How many record want to skip',
    example: 0,
  })
  @IsNumber()
  @IsOptional()
  skip?: number;

  @ApiProperty({
    description: 'How many record want to take',
    example: 10,
  })
  @IsNumber()
  @IsOptional()
  take?: number;

  @ApiProperty({
    description: 'Sort field createdAt',
  })
  @IsOptional()
  @IsEnum(SortEnum)
  sort?: SortEnum;
}
