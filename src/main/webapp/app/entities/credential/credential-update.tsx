import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IIdentityProvider } from 'app/shared/model/identity-provider.model';
import { getEntities as getIdentityProviders } from 'app/entities/identity-provider/identity-provider.reducer';
import { IServiceProvider } from 'app/shared/model/service-provider.model';
import { getEntities as getServiceProviders } from 'app/entities/service-provider/service-provider.reducer';
import { getEntity, updateEntity, createEntity, reset } from './credential.reducer';
import { ICredential } from 'app/shared/model/credential.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CredentialUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const identityProviders = useAppSelector(state => state.identityProvider.entities);
  const serviceProviders = useAppSelector(state => state.serviceProvider.entities);
  const credentialEntity = useAppSelector(state => state.credential.entity);
  const loading = useAppSelector(state => state.credential.loading);
  const updating = useAppSelector(state => state.credential.updating);
  const updateSuccess = useAppSelector(state => state.credential.updateSuccess);
  const handleClose = () => {
    props.history.push('/credential' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getIdentityProviders({}));
    dispatch(getServiceProviders({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...credentialEntity,
      ...values,
      identityProvider: identityProviders.find(it => it.id.toString() === values.identityProvider.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...credentialEntity,
          identityProvider: credentialEntity?.identityProvider?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="simplecredentialsmanagerApp.credential.home.createOrEditLabel" data-cy="CredentialCreateUpdateHeading">
            <Translate contentKey="simplecredentialsmanagerApp.credential.home.createOrEditLabel">Create or edit a Credential</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="credential-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('simplecredentialsmanagerApp.credential.profile')}
                id="credential-profile"
                name="profile"
                data-cy="profile"
                type="text"
              />
              <ValidatedField
                label={translate('simplecredentialsmanagerApp.credential.enabled')}
                id="credential-enabled"
                name="enabled"
                data-cy="enabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('simplecredentialsmanagerApp.credential.username')}
                id="credential-username"
                name="username"
                data-cy="username"
                type="text"
              />
              <ValidatedField
                label={translate('simplecredentialsmanagerApp.credential.password')}
                id="credential-password"
                name="password"
                data-cy="password"
                type="text"
              />
              <ValidatedField
                id="credential-identityProvider"
                name="identityProvider"
                data-cy="identityProvider"
                label={translate('simplecredentialsmanagerApp.credential.identityProvider')}
                type="select"
              >
                <option value="" key="0" />
                {identityProviders
                  ? identityProviders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/credential" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CredentialUpdate;
