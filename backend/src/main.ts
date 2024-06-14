import { SwaggerBuildFactory } from '@app/common';
import { Logger, ValidationPipe } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { NestFactory } from '@nestjs/core';
import { json as expressJson, urlencoded as expressUrlEncoded } from 'express';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule, { bufferLogs: true });
  app.enableCors();
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidUnknownValues: true,
      transform: true,
      transformOptions: {
        enableImplicitConversion: true,
      },
    }),
  );

  app.use(expressJson({ limit: '50mb' }));
  app.use(expressUrlEncoded({ limit: '50mb', extended: true }));

  SwaggerBuildFactory(app);

  const logger = new Logger('NestApplication');
  const configService = app.get(ConfigService);
  await app.listen(configService.getOrThrow('PORT'), async () => {
    const prefix = configService.getOrThrow('PREFIX_NAME');
    logger.warn(`Swagger is running on: ${await app.getUrl()}/${prefix}/docs`);
    logger.warn(`Application (HTTP) is running on: ${await app.getUrl()}`);
  });
}
bootstrap();
