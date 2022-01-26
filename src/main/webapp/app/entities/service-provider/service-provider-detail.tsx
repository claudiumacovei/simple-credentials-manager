import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './service-provider.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ServiceProviderDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const serviceProviderEntity = useAppSelector(state => state.serviceProvider.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="serviceProviderDetailsHeading">
          <Translate contentKey="simplecredentialsmanagerApp.serviceProvider.detail.title">ServiceProvider</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{serviceProviderEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="simplecredentialsmanagerApp.serviceProvider.name">Name</Translate>
            </span>
          </dt>
          <dd>{serviceProviderEntity.name}</dd>
          <dt>
            <Translate contentKey="simplecredentialsmanagerApp.serviceProvider.credential">Credential</Translate>
          </dt>
          <dd>
            {serviceProviderEntity.credentials
              ? serviceProviderEntity.credentials.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {serviceProviderEntity.credentials && i === serviceProviderEntity.credentials.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/service-provider" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/service-provider/${serviceProviderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ServiceProviderDetail;
