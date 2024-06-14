import { FileSystemService } from '@app/helper';
import { Injectable } from '@nestjs/common';
import { FILE_DESTINATION } from './constant/file-destination.constant';
import { FileUploadRepository } from './file-upload.repository';
import { FileUpload } from './entity/file-upload.entity';

@Injectable()
export class FileUploadService {
  constructor(
    private readonly fileSystemService: FileSystemService,
    private readonly fileUploadRepository: FileUploadRepository
  ) { }

  async saveFile(
    bucket: string,
    { originalname, buffer }: Express.Multer.File,
  ) {
    return await this.fileSystemService.saveFile(bucket, originalname, buffer);
  }
  
  async create(payload: FileUpload) {
    return await this.fileUploadRepository.create(payload);
  }
  
  async listByUploader(upload_by: string) {
    return await this.fileUploadRepository.listByUploader(upload_by);
  }
  
  async delete(id: number) {
    return await this.fileUploadRepository.deleteById(id);
  }
}
