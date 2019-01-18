// vendor
import React, { PureComponent } from 'react';
import { NavLink, withRouter } from 'react-router-dom';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import logo from './game-logo.png';
import avaDefault from './icon_ava_default.svg';

// proj
import { book } from '../../routes';
import { getGameConnectionString } from '../../utils';

// own
import Styles from './styles.module.css';

class HeaderComponent extends PureComponent {
    render() {
        const { server, logout, email, code } = this.props;

        return (
            <header>
                <div className={ Styles.container }>
                    <NavLink className={ Styles.logoContainer } to={ book.home }>
                        <img className={ Styles.logo } src={ logo } alt='' />
                        <span>EPAM BOT CHALLENGE</span>
                    </NavLink>
                    { server && (
                        <div className={ Styles.serverInfo }>
                            <div className={ Styles.serverName }>
                                Сервер: { server }
                            </div>
                            <CopyToClipboard
                                text={ getGameConnectionString(
                                    server,
                                    code,
                                    email,
                                ) }
                            >
                                <button className={ Styles.copyButton }>
                                    Copy
                                </button>
                            </CopyToClipboard>
                        </div>
                    ) }

                    <ul>
                        <li>
                            <NavLink
                                className={ Styles.navMenu }
                                activeClassName={ Styles.activeMenu }
                                to={ book.home }
                            >
                                Головна
                            </NavLink>
                        </li>
                        <li>
                            <NavLink
                                className={ Styles.navMenu }
                                activeClassName={ Styles.activeMenu }
                                to={ book.board }
                            >
                                Трансляція
                            </NavLink>
                        </li>

                        { server && (
                            <li>
                                <img
                                    className={ Styles.avatar }
                                    src={ avaDefault }
                                    alt=''
                                />
                                <div onClick={ () => logout() }>Вийти</div>
                            </li>
                        ) }
                        { !server && (
                            <li>
                                <NavLink
                                    className={ Styles.navMenu }
                                    activeClassName={ Styles.activeMenu }
                                    to={ book.register }
                                >
                                    Реєстрація
                                </NavLink>
                            </li>
                        ) }

                        { !server && (
                            <li>
                                <NavLink
                                    className={ Styles.navMenu }
                                    activeClassName={ Styles.activeMenu }
                                    to={ book.login }
                                >
                                    <img
                                        className={ Styles.avatar }
                                        src={ avaDefault }
                                        alt=''
                                    />
                                    Авторизація
                                </NavLink>
                            </li>
                        ) }
                    </ul>
                </div>
            </header>
        );
    }
}

export const Header = withRouter(HeaderComponent);
