import { IsNotEmpty, IsString } from "class-validator";
import { FileUpload } from "../entity/file-upload.entity";

export class FileUploadDto {
  @IsNotEmpty()
  @IsString()
  description: string;

  @IsNotEmpty()
  @IsString()
  upload_by: string;

  intoFileUpload(): FileUpload {
    const file = new FileUpload()
    file.description = this.description;
    file.upload_by = this.upload_by;
    return file;
  }
}
