import React, { Component } from 'react';
import Select from 'react-select';
import classNames from 'classnames';
import styles from './styles.module.css';

const { selectWrap, select, selectValid, selectError  } = styles;

const customSelectStyles = {
    control: (provided) => ({
        ...provided,
        borderRadius: 0,
        border: 0,
        padding: '5px 0px',
        fontSize: '12px',
        background: '#263852',

        '&:hover': {
            border: 0,
            boxShadow: 'none',
        },
    }),
    menu: (provided) => ({
        ...provided,
        borderRadius: 0,
        border: '1px solid #41c7dc',
        fontSize: '12px',
        background: '#263852',
    }),
    option: (provided) => ({
        ...provided,
        borderRadius: 0,
    }),
    singleValue: (provided) => ({
        ...provided,
        color: '#a1b0bb',
    }),
}

export class CustomSelectComponent extends Component {

    render() {
        const { field, form, placeholder, options } = this.props;
        const { touched, errors } = form;
        const isCurrentFieldNotValid = touched[ field.name ] && errors[ field.name ];
        const isCurrentFieldValid = touched[ field.name ] && !errors[ field.name ];

        return (
            <div className={ selectWrap }>
                <Select
                    className={ classNames(select, { [ selectError ]: isCurrentFieldNotValid, [ selectValid ]: isCurrentFieldValid  }) }
                    styles={ customSelectStyles }
                    options={ options }
                    multi={ false }
                    onChange={ (option) => form.setFieldValue(field.name, option.value) }
                    placeholder={ placeholder }
                />
            </div>
        );
    }
}
