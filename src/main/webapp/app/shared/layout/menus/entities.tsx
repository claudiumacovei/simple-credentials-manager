import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/credential">
      <Translate contentKey="global.menu.entities.credential" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/identity-provider">
      <Translate contentKey="global.menu.entities.identityProvider" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/service-provider">
      <Translate contentKey="global.menu.entities.serviceProvider" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
