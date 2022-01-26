export interface IIdentityProvider {
  id?: number;
  name?: string | null;
}

export const defaultValue: Readonly<IIdentityProvider> = {};
