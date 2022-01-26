import { IIdentityProvider } from 'app/shared/model/identity-provider.model';
import { IServiceProvider } from 'app/shared/model/service-provider.model';

export interface ICredential {
  id?: number;
  profile?: string | null;
  enabled?: boolean | null;
  username?: string | null;
  password?: string | null;
  identityProvider?: IIdentityProvider | null;
  serviceProviders?: IServiceProvider[] | null;
}

export const defaultValue: Readonly<ICredential> = {
  enabled: false,
};
