import { IncludeArgs } from '../types/include.type';

export class FindUniqueDto {
  uniqueField: string;
  uniqueFieldValue: any;
  include?: IncludeArgs;
}
