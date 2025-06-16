import { Outlet } from 'umi';
import styles from './index.less';

export default function Content() {
    return (
      <div className={styles.contentWrapper}>
        <Outlet />
      </div>
    );
  }
  