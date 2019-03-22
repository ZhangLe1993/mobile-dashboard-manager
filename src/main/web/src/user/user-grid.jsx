import React from 'react'
import {Table, Input, Button, Tag} from 'antd';
import 'antd/dist/antd.css';
import axios from 'axios'
import ActBtn from './activation-code.jsx';
import BanUserToggle from './ban-user.jsx';

const Search = Input.Search;

class UserTable extends React.Component {
    constructor(pros) {
        super(pros);
        this.state = {
            columns: [
                {title: 'ID', dataIndex: 'id', key: 'id'},
                {
                    title: '用户名', dataIndex: 'name', key: 'name', render: (text, row) => {
                        if (row.isAdmin) {
                            return (<Tag color="#108ee9">{text}</Tag>);
                        } else {
                            return text;
                        }
                    }
                },
                {title: '工号', dataIndex: 'employeeNo', key: 'employeeNo'},
                {title: '微信标识', dataIndex: 'openId', key: 'openId'},
                {
                    title: '激活码',
                    dataIndex: 'activationCode',
                    key: 'activationCode',
                    render: (text, record) => <ActBtn active={record.active} uid={record.id}
                                                      employee={record.employeeNo} enable={record.enable}
                                                      afterActive={() => this.searchKeyFun(this.state.searchKey)}/>
                },
                // {title: '是否已激活', dataIndex: 'active', key: 'active',render:text=><Checkbox toggle checked={text}/>},
                {
                    title: '是否可用',
                    dataIndex: 'enable',
                    key: 'enable',
                    render: (text, record) => <BanUserToggle uid={record.id} enable={record.enable}/>
                },
            ],
            data: [],
            total: 1,
            searchKey: '',
            pageIndex: 1,
            pageSize: 10
        };
    }

    searchUser(key, pageIndex, pageSize) {
        this.state.pageIndex = pageIndex;
        this.state.pageSize = pageSize;
        axios.get('/back/user', {
            params: {
                key: key,
                page_index: pageIndex,
                page_size: pageSize,
            }
        }).then((response) => {
            for (let index = 0; index < response.data.data.length; index++) {
                response.data.data[index].key = pageIndex * pageSize + index;
            }
            this.setState({data: response.data.data, total: response.data.total});
        });
    }

    searchKeyFun = (v) => {
        this.state.searchKey = v;
        this.state.pageIndex = 1;
        this.searchUser(v, this.state.pageIndex, this.state.pageSize);
    };

    componentDidMount() {
        this.searchUser('', 1, 10);
    }

    clearCache = () => {
        axios.get("/back/clear_md");
    };

    render() {
        return (
            <div>
                <Button type="primary" onClick={this.clearCache}>清理缓存</Button>
                <Search placeholder="input search text"
                        enterButton="Search"
                        size="large" onSearch={this.searchKeyFun}/>
                <Table columns={this.state.columns} dataSource={this.state.data} pagination={{
                    current: this.state.pageIndex,
                    total: this.state.total, page_size: this.state.pageSize, onChange: (a, b) => {
                        this.searchUser(this.state.searchKey, a, b)
                    }
                }}/>
            </div>
        )
    }
}


export default UserTable