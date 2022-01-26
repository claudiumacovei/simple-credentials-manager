import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ICredential } from 'app/shared/model/credential.model';
import { getEntities as getCredentials } from 'app/entities/credential/credential.reducer';
import { getEntity, updateEntity, createEntity, reset } from './service-provider.reducer';
import { IServiceProvider } from 'app/shared/model/service-provider.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ServiceProviderUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const credentials = useAppSelector(state => state.credential.entities);
  const serviceProviderEntity = useAppSelector(state => state.serviceProvider.entity);
  const loading = useAppSelector(state => state.serviceProvider.loading);
  const updating = useAppSelector(state => state.serviceProvider.updating);
  const updateSuccess = useAppSelector(state => state.serviceProvider.updateSuccess);
  const handleClose = () => {
    props.history.push('/service-provider' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getCredentials({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...serviceProviderEntity,
      ...values,
      credentials: mapIdList(values.credentials),
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
          ...serviceProviderEntity,
          credentials: serviceProviderEntity?.credentials?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="simplecredentialsmanagerApp.serviceProvider.home.createOrEditLabel" data-cy="ServiceProviderCreateUpdateHeading">
            <Translate contentKey="simplecredentialsmanagerApp.serviceProvider.home.createOrEditLabel">
              Create or edit a ServiceProvider
            </Translate>
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
                  id="service-provider-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('simplecredentialsmanagerApp.serviceProvider.name')}
                id="service-provider-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('simplecredentialsmanagerApp.serviceProvider.credential')}
                id="service-provider-credential"
                data-cy="credential"
                type="select"
                multiple
                name="credentials"
              >
                <option value="" key="0" />
                {credentials
                  ? credentials.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/service-provider" replace color="info">
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

export default ServiceProviderUpdate;
