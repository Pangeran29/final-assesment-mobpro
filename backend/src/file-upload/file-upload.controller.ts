import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Req,
  UploadedFile,
  UseInterceptors
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiBearerAuth,
  ApiBody,
  ApiConsumes,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { FileUploadDto } from './dto/upload-file.dto';
import { FileUploadService } from './file-upload.service';
import { Request } from 'express';

@ApiBearerAuth()
@ApiTags('file-upload')
@Controller('file-upload')
export class FileUploadController {
  constructor(private readonly fileUploadService: FileUploadService) { }

  @ApiOperation({
    description: `
      Used for client to upload file, required uploader name (string) as identifier of 
      the uploader.
    `,
  })
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        file: {
          type: 'string',
          format: 'binary',
        },
        upload_by: {
          type: 'string',
        },
        description: {
          type: 'string',
        },
      },
    },
  })
  @Post()
  @UseInterceptors(FileInterceptor('file'))
  async uploadFile(
    @UploadedFile() file: Express.Multer.File,
    @Body() dto: FileUploadDto,
  ) {
    const data = await this.fileUploadService.saveFile(
      dto.upload_by,
      file,
    );

    let file_upload = dto.intoFileUpload();
    file_upload.file_location = data.filePath;
    return await this.fileUploadService.create(file_upload);
  }

  @ApiOperation({
    description: `Get available public file by uploader`,
  })
  @Get('/:upload_by')
  async listByUploader(
    @Param('upload_by') upload_by: string,
    @Req() request: Request,
  ) {
    let files =  await this.fileUploadService.listByUploader(upload_by);
    const server_uri = `${request.protocol}://${request.get('host')}/`;
    files = files.map(val => {
      val.file_location = server_uri + val.file_location;
      return val
    });
    return files;
  }

  @ApiOperation({
    description: `Delete file by id`,
  })
  @Delete('/:id')
  async delete(
    @Param('id') id: number,
  ) {
    return await this.fileUploadService.delete(id);
  }
}
