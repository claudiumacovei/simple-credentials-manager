import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './identity-provider.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const IdentityProviderDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const identityProviderEntity = useAppSelector(state => state.identityProvider.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="identityProviderDetailsHeading">
          <Translate contentKey="simplecredentialsmanagerApp.identityProvider.detail.title">IdentityProvider</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{identityProviderEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="simplecredentialsmanagerApp.identityProvider.name">Name</Translate>
            </span>
          </dt>
          <dd>{identityProviderEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/identity-provider" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/identity-provider/${identityProviderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IdentityProviderDetail;
