import { IncludeArgs } from '../types/include.type';
import { BaseQueryFindManyDto } from './find-all-record.base.dto';

export class FindManyDto {
  baseQueryFindManyDto: BaseQueryFindManyDto;
  include?: IncludeArgs;
  where?: object;
}
