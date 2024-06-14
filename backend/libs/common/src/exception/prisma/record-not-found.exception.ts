import { Logger, NotFoundException } from '@nestjs/common';
import { NOT_FOUND_EXC_MSG } from '../message';

export class RecordNotFoundException extends NotFoundException {
  private readonly logger = new Logger(RecordNotFoundException.name);
  constructor(msg: NOT_FOUND_EXC_MSG) {
    super(msg);
    this.logger.error(msg);
  }
}
