import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Credential from './credential';
import IdentityProvider from './identity-provider';
import ServiceProvider from './service-provider';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}credential`} component={Credential} />
      <ErrorBoundaryRoute path={`${match.url}identity-provider`} component={IdentityProvider} />
      <ErrorBoundaryRoute path={`${match.url}service-provider`} component={ServiceProvider} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
