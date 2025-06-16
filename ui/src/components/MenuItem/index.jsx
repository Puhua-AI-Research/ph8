import React from 'react';
import classNames from 'classnames';
import styles from './index.less'

export default ({ name, icon, label, type, onClick, path, collapsed, selected = false }) => {
    if (type === 'divider') return <div className={styles.menuDivider}></div>
    if (type === 'group') return <div className={classNames(styles.menuGroup, { [styles.collapsed]: collapsed })}>{label}</div>

    return (
        <div className={classNames(styles.menuItem, { [styles.selected]: selected, [styles.collapsed]: collapsed })} onClick={() => onClick({ name, path })}>
            <span>{icon}</span>
            {
                !collapsed && (
                    <span>{label}</span>
                )
            }
        </div>
    )
}




