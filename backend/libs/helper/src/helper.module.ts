import { Module } from '@nestjs/common';
import { FileSystemModule } from './file-system/file-system.module';

@Module({
  imports: [FileSystemModule],
})
export class HelperModule { }
