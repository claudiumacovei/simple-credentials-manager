import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Credential from './credential';
import CredentialDetail from './credential-detail';
import CredentialUpdate from './credential-update';
import CredentialDeleteDialog from './credential-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CredentialUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CredentialUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CredentialDetail} />
      <ErrorBoundaryRoute path={match.url} component={Credential} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CredentialDeleteDialog} />
  </>
);

export default Routes;
