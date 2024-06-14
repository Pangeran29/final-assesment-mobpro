import { File } from "@prisma/client";

export class FileUpload implements File {
  id: number;
  upload_by: string;
  file_location: string;
  description: string;
  createdAt: Date;
  updatedAt: Date;
}