import {
  CallHandler,
  ExecutionContext,
  Injectable,
  NestInterceptor,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class ResponseInterceptor implements NestInterceptor {
  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    return next.handle().pipe(
      map((response) => {
        if (response.message) {
          return {
            statusCode: context.switchToHttp().getResponse().statusCode,
            message: response?.message,
            data: response?.data,
          };
        } else {
          return {
            statusCode: context.switchToHttp().getResponse().statusCode,
            message: response?.message,
            data: response?.data || response,
          };
        }
      }),
    );
  }
}
