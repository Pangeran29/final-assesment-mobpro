import { INestApplication } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  DocumentBuilder,
  SwaggerCustomOptions,
  SwaggerDocumentOptions,
  SwaggerModule,
} from '@nestjs/swagger';

export function SwaggerBuildFactory(app: INestApplication) {
  const configService = app.get(ConfigService);

  const appName = configService.get('APP_NAME');
  const prefix = configService.get('PREFIX_NAME');

  const swaggerConfig = new DocumentBuilder()
    .setTitle(appName)
    .setDescription(`The ${appName} API Documentation`)
    .setVersion('1.0')
    .addBearerAuth()
    .build();

  const swaggerDocumentOptions: SwaggerDocumentOptions = {
    operationIdFactory: (controllerKey: string, methodKey: string) => methodKey,
    ignoreGlobalPrefix: true,
  };

  const document = SwaggerModule.createDocument(
    app,
    swaggerConfig,
    swaggerDocumentOptions,
  );

  const customOptions: SwaggerCustomOptions = {
    customSiteTitle: `${appName} API Documentation`,
    swaggerOptions: {
      tagsSorter: 'alpha',
      operationsSorter: 'alpha',
    },
  };

  SwaggerModule.setup(`/${prefix}/docs`, app, document, customOptions);
}
