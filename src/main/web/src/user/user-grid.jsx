import React from 'react'
import {Table, Input, Button, Tag, Modal, Checkbox, Row, Col} from 'antd';
import 'antd/dist/antd.css';
import axios from 'axios'
import ActBtn from './activation-code.jsx';
import BanUserToggle from './ban-user.jsx';

const Search = Input.Search;
const CheckboxGroup = Checkbox.Group;

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
                // {
                //     title: '权限',
                //     dataIndex: 'group',
                //     key: 'group',
                //     render: (text, record) => <div>{record.id}</div>
                // },
                {
                    title: '更新权限',
                    dataIndex: 'action',
                    key: 'action',
                    render: (text, record) => (
                     <span>
                        <a href="javascript:;" onClick={() => {
                            this.setState({ visible: true, selectedEmployeeNo: record.employeeNo }, () => {
                                this.fetchGroupData(record.employeeNo);
                            });
                        }}>更新权限组</a>
                      </span>
                      )
                },
            ],
            data: [],
            total: 1,
            searchKey: '',
            pageIndex: 1,
            pageSize: 10,
            confirmLoading: false, // 点击模态框确定loading状态
            visible: false, // 模态框是否展示
            allGroup: [
            //   {id: 4, groupValue: 'C2B', desc: ''},
            //   {id: 5, groupValue: 'B2B', desc: ''},
            ],
            personalGroup: [],
            selectedEmployeeNo: null,
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
        this.fetchGroupData();
    }

    clearCache = () => {
        axios.get("/back/clear_md");
    };

    triggerNotice = (no) => {
        axios.get("/back/notice/trigger", {
            params: {
                "employee_no": no
            }
        });
    };

    fetchGroupData = (no = null) => {
        axios.get("/back/user/group/list", {
            params: {
                "employee_no": no
            }
        }).then(res => {
            const data = res.data.data;
            if (no) {
                const checkedValue = data.map(it => it.id);
                this.setState({ personalGroup: data, checkedValue });
            } else {
                this.setState({ allGroup: data });
            }
        });
    }

    updateGroupData = () => {
        const { selectedEmployeeNo, checkedValue } = this.state;
        axios.post("/back/user/empower", {
            employee_no: selectedEmployeeNo,
            group_ids: checkedValue,
        }).then(res => {
            this.setState({ confirmLoading: false });
        }).catch(() => {
            this.setState({ confirmLoading: false });
        });
    }

    handleOk = () => {
        this.setState({ visible: false, confirmLoading: true }, () => {
          this.updateGroupData();
        });
    }

    handleCancel = () => {
        this.setState({ visible: false });
    }

    handleGroupChange = (checkedValue) => {
        // console.log(checkedValue, '-checkedValue-');
        this.setState({ checkedValue });
    }

    render() {
        const { allGroup, personalGroup, checkedValue } = this.state;
        return (
            <div>
                <Button type="primary" onClick={this.clearCache}>清理缓存</Button>
                <Search placeholder="工号" onSearch={this.triggerNotice} style={{width:250}} enterButton="发送模版消息"/>
                <Search placeholder="input search text"
                        enterButton="Search"
                        size="large" onSearch={this.searchKeyFun}/>
                <Table columns={this.state.columns} dataSource={this.state.data} pagination={{
                    current: this.state.pageIndex,
                    total: this.state.total, page_size: this.state.pageSize, onChange: (a, b) => {
                        this.searchUser(this.state.searchKey, a, b)
                    }
                }}/>
                <Modal
                  title="更新权限"
                  cancelText="取消"
                  okText="确定"
                  visible={this.state.visible}
                  confirmLoading={this.state.confirmLoading}
                  onOk={this.handleOk}
                  onCancel={this.handleCancel}
                >
                <CheckboxGroup onChange={this.handleGroupChange} value={checkedValue}>
                  {
                    allGroup && allGroup.map(it => {
                      return (
                        <Row key={it.groupKey} style={{ marginBottom: '10px'}}>
                          <Col span={24}><Checkbox value={it.id}>{it.description}</Checkbox></Col>
                        </Row>
                      );
                    })
                  }
                </CheckboxGroup>
                </Modal>
            </div>
        )
    }
}


export default UserTable