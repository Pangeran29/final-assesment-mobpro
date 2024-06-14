import { BaseRepository } from '@app/common';
import { Injectable } from '@nestjs/common';
import { File } from '@prisma/client';

@Injectable()
export class FileUploadRepository extends BaseRepository<File> {
  constructor() {
    super('File')
  }
  async listByUploader(upload_by: string) {
    return await this.prisma.file.findMany({ where: { upload_by: { equals: upload_by } } });
  }
}
