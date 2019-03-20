import React from 'react'
import { Checkbox } from 'semantic-ui-react'
import Axios from 'axios'

class BanUserToggle extends React.Component {

    constructor(props){
        super(props);
        this.state = {enable: props.enable};
        this.banUser = () => {
            let flag=!this.state.enable;
            let uid = this.props.uid;
            let data = {
                params: {
                    uid: uid,
                    enable: flag
                }
            };
            Axios.get("/back/user/ban", data);
            this.setState({enable:flag});
        };
    }

    render() {
        return (
            <Checkbox toggle checked={this.state.enable} onChange={this.banUser}/>
        );
    }
}

export default BanUserToggle;