import React from 'react'
import {Button, Confirm, Image, Modal} from 'semantic-ui-react'
import Axios from 'axios'

class ActivationButton extends React.Component {

    state = {updateConfirmOpen: false, activationCodeImgOpen: false, activationImg: ''};


    updateCodeOpen = () => this.setState({updateConfirmOpen: true});
    updateCodeClose = () => this.setState({updateConfirmOpen: false});
    updateCode = () => {
        let employeeNo =this.props.employee;
        let data = new FormData();
        data.append('employee-no', employeeNo);
        Axios.post("/back/update-activation-code", data, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then(() => {
            this.updateCodeClose();
        });
    };

    viewCode = () => {
        this.setState({
            activationImg: '/back/activation-img/' + this.props.uid,
            activationCodeImgOpen: true
        });
    };

    renderBtn = () => {
        if (this.props.active) {
            return (
                <Button positive onClick={this.viewCode}>查看激活码</Button>
            )
        } else if (this.props.uid == null) {
            return (
                <Button positive onClick={this.updateCodeOpen}>生成激活码</Button>
            )
        } else {
            return (
                <Button.Group>
                    <Button positive onClick={this.viewCode}>查看</Button>
                    <Button.Or text='or'/>
                    <Button positive onClick={this.updateCodeOpen}>更新</Button>
                </Button.Group>
            )
        }
    };

    render() {
        return (
            <div>
                {this.renderBtn()}
                <Confirm open={this.state.updateConfirmOpen} onCancel={this.updateCodeClose}
                         onConfirm={this.updateCode}/>

                <Modal size='mini' open={this.state.activationCodeImgOpen} onClose={() => {
                    this.setState({activationCodeImgOpen: false})
                }}>
                    <Modal.Header>激活码（激活成功后立即失效）</Modal.Header>
                    <Modal.Content>
                        <Image centered wrapped size='medium' src={this.state.activationImg}/>
                    </Modal.Content>
                </Modal>
            </div>
        )
    }
}

export default ActivationButton;

