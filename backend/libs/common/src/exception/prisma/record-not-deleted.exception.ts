import { ConflictException, Logger } from '@nestjs/common';

export class RecordNotDeletedException extends ConflictException {
  private readonly logger = new Logger(RecordNotDeletedException.name);
  constructor(error?: any) {
    let msg: any = 'Fail to create record.';

    if (error.code === 'P2002') {
      const scope = error?.meta?.target;
      msg = `Unique constraint exception at field ${scope}.`;
    }

    if (error.code === 'P2003') {
      msg = `Child record not found at ${error?.meta?.field_name}.`;
    }

    if (error.code === 'P2025') {
      msg = `Fail to connect to some record. Please check the id and make sure the record exist.`;
    }

    super(msg);
    this.logger.error(msg, error);
  }
}
