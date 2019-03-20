import React from 'react'
import {Switch} from 'semantic-ui-react'
import Axios from 'axios'

class BanUserToggle extends React.Component {
    state = {enable: this.props.enable};
    banUser = (flag) => {
        let uid = this.props.uid;
        let data = {
            params: {
                uid: uid,
                enable: flag
            }
        };
        Axios.get("/back/user/ban", data);
    };

    render() {
        return (
            <Switch checked={this.state.enable} onChange={this.banUser()}/>
        );
    }
}

export default BanUserToggle;