import React from 'react'
import {Header, Table, Checkbox} from 'semantic-ui-react'
import axios from 'axios'
import ActBtn from './activation-code.jsx'

class UserTable extends React.Component {
    constructor(pros) {
        super(pros);
        this.state = {
            headers: ['ID', '用户名', '工号', '微信标识', '激活码', '是否已激活'],
            data: []
        }
    }

    componentDidMount() {
        axios.get('/back/user').then((response) => {
            this.setState({data: response.data});
        });
    }

    render() {
        let header = () => {
            let arr = [];
            for (let hn of this.state.headers) {
                if (hn) {
                    arr.push(<Table.HeaderCell textAlign='center'>{hn}</Table.HeaderCell>)
                }
            }
            return arr;
        };

        let row = () => {
            let arr = [];
            for (let row of this.state.data) {
                if (row) {
                    arr.push(
                        <Table.Row>
                            <Table.Cell>
                                <Header as='h2' textAlign='center'>
                                    {row.id}
                                </Header>
                            </Table.Cell>
                            <Table.Cell textAlign='center'>
                                {row.name}
                            </Table.Cell>
                            <Table.Cell textAlign='center'>
                                {row.employeeNo}
                            </Table.Cell>
                            <Table.Cell textAlign='center'>
                                {row.openId}
                            </Table.Cell>
                            <Table.Cell textAlign='center'>
                                <ActBtn code={row.activationCode} uid={row.id}/>
                            </Table.Cell>
                            <Table.Cell textAlign='center'>
                                <Checkbox toggle checked={row.active}/>
                            </Table.Cell>
                        </Table.Row>
                    )
                }
            }
            return arr;
        };

        return (
            <Table celled padded>
                <Table.Header>
                    <Table.Row>
                        {header()}
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {row()}
                </Table.Body>
            </Table>
        )
    }
}


export default UserTable