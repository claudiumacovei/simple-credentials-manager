import { ICredential } from 'app/shared/model/credential.model';

export interface IServiceProvider {
  id?: number;
  name?: string | null;
  credentials?: ICredential[] | null;
}

export const defaultValue: Readonly<IServiceProvider> = {};
