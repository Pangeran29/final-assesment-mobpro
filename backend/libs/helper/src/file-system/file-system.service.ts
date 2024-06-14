import { Injectable, InternalServerErrorException } from '@nestjs/common';
import * as fsPromises from 'fs/promises';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class FileSystemService {
  async saveFile(
    destination: any,
    fileName: any,
    data: any,
  ): Promise<{ filePath: string }> {
    const directory = `storage/file/${destination}`;
    const isDirectoryExist = await this.checkPathExist(directory);
    if (!isDirectoryExist) {
      await fsPromises.mkdir('./' + directory, { recursive: true });
    }

    try {
      const uid = uuidv4();
      const fullPath = `${directory}/${uid}-${fileName}`;
      await fsPromises.writeFile(fullPath, data, 'utf8');
      return { filePath: fullPath };
    } catch (error) {
      throw new InternalServerErrorException(error);
    }
  }

  async deleteFile(filePath: string): Promise<boolean> {
    const isPathExist = await this.checkPathExist(filePath);
    if (isPathExist) {
      try {
        fsPromises.unlink(filePath);
      } catch (error) {
        throw new InternalServerErrorException(error);
      }
    }
    return true;
  }

  async checkPathExist(path: string): Promise<boolean> {
    return await fsPromises
      .access(path, fsPromises.constants.F_OK)
      .then(() => true)
      .catch(() => false);
  }
}
