import { FileSystemModule } from '@app/helper';
import { Module } from '@nestjs/common';
import { FileUploadController } from './file-upload.controller';
import { FileUploadService } from './file-upload.service';
import { FileUploadRepository } from './file-upload.repository';

@Module({
  controllers: [FileUploadController],
  providers: [FileUploadService, FileUploadRepository],
  imports: [FileSystemModule],
})
export class FileUploadModule { }
