import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import IdentityProvider from './identity-provider';
import IdentityProviderDetail from './identity-provider-detail';
import IdentityProviderUpdate from './identity-provider-update';
import IdentityProviderDeleteDialog from './identity-provider-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={IdentityProviderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={IdentityProviderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={IdentityProviderDetail} />
      <ErrorBoundaryRoute path={match.url} component={IdentityProvider} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={IdentityProviderDeleteDialog} />
  </>
);

export default Routes;
