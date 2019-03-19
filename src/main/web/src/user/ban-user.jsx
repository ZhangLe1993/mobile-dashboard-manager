import React from 'react'
import {Checkbox} from 'semantic-ui-react'
import Axios from 'axios'

class BanUserToggle extends React.Component {
    state = {enable: this.props.enable};
    banUser = () => {
        let flag = !this.state.enable;
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
            <Checkbox toggle checked={this.state.enable} onChange={this.banUser()}/>
        );
    }
}

export default BanUserToggle;