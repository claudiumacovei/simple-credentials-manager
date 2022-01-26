import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import credential from 'app/entities/credential/credential.reducer';
// prettier-ignore
import identityProvider from 'app/entities/identity-provider/identity-provider.reducer';
// prettier-ignore
import serviceProvider from 'app/entities/service-provider/service-provider.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  credential,
  identityProvider,
  serviceProvider,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
