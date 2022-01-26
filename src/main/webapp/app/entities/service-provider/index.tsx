import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ServiceProvider from './service-provider';
import ServiceProviderDetail from './service-provider-detail';
import ServiceProviderUpdate from './service-provider-update';
import ServiceProviderDeleteDialog from './service-provider-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ServiceProviderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ServiceProviderUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ServiceProviderDetail} />
      <ErrorBoundaryRoute path={match.url} component={ServiceProvider} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ServiceProviderDeleteDialog} />
  </>
);

export default Routes;
