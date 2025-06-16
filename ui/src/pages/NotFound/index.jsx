import React from 'react';
import { Button, Result } from 'antd';
import { useNavigate } from 'umi';

export default () => {
    const nav = useNavigate()

    return (
        <Result
            status="404"
            title="404"
            subTitle="Sorry, the page you visited does not exist."
            extra={<Button type="primary" onClick={() => nav('/')}>Back Home</Button>}
        />
    )
}
