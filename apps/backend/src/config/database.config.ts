import { registerAs } from '@nestjs/config';
import { TypeOrmModuleOptions } from '@nestjs/typeorm';

export default registerAs(
  'database',
  (): TypeOrmModuleOptions => ({
    type: 'postgres',
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT, 10) || 5432,
    username: process.env.DB_USERNAME || 'eduforum',
    password: process.env.DB_PASSWORD || 'eduforum12',
    database: process.env.DB_DATABASE || 'eduforum',
    entities: [__dirname + '/../**/*.entity{.ts,.js}'],
    synchronize: process.env.DB_SYNC === 'true' || false,
    logging: process.env.DB_LOGGING === 'true' || false,
    migrations: [__dirname + '/../migrations/**/*{.ts,.js}'],
    migrationsRun: false,
    ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
  }),
);
