/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

pages = pages || {};

pages.register = function() {
    setup.contextPath = getSettings('contextPath');
    setup.waitApprove = getSettings('waitApprove');

    initRegistration(setup.waitApprove, setup.contextPath);

    initHotkeys();
}

function initRegistration(waitApprove, contextPath) {
    var disable = function(status) {
        $("#submit").prop("disabled", status);
        $("#name").prop("disabled", status);
        $("#fullName").prop("disabled", status);
        $("#readable-name").prop("disabled", status);
        $("#password").prop("disabled", status);
        $("#gitHubUsername").prop("disabled", status);
        $("#slackEmail").prop("disabled", status);
    }

    var KEYS = {
        userData: {
            email: "registration-email",
            fullName: "registration-fullName",
            readableName: "registration-readableName",
            github: "registration-gitHubUsername",
            slackEmail: "registration-slackEmail",
            data: "registration-data",
            data1: "registration-data1",
            data2: "registration-data2",
            data3: "registration-data3",
            data4: "registration-data4"
        }
    };

    function display(element, isVisible) {
        element = $(element);
        if (isVisible) {
            element.removeAttr('hidden');
            element.show();
        } else {
            element.attr('hidden', 'hidden');
            element.hide();
        }
    }

    function configureFormFromAdminSettings(onFinish) {
        var general = new AdminSettings(contextPath, 'general', 'registration');

        general.load(
            function(data) {
                onLoad(data);
            }, function(error) {
                onLoad(null);            
            });
        
        var onLoad = function(data) {
            if ($.isEmptyObject(data)) {
                data = defaultRegistrationSettings();
            }

            // will display in fillFormFromLocalStorage
            // display('#gameType', data.showGames);
            display('#fullName', data.showNames);
            display('#readableName', data.showNames);
            display('#data1', data.showData1);
            display('#data2', data.showData2);
            display('#data3', data.showData3);
            display('#data4', data.showData4);
            display('#gitHubUsername', data.showNames);
            display('#slackEmail', data.showNames);

            fillFormFromLocalStorage(data);

            if (!!onFinish) {
                onFinish();
            }
        } 
    }

    function loadRegistrationPage() {
        var checkEls = {};

        var validateEmail = function (email) {
            var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,6})?$/;
            return emailReg.test(email);
        };

        checkEls['email'] = function (value) {
            return value == '' || !validateEmail(value);
        };

        var notEmpty = function (value) {
            return value == '' || value.length == 0;
        };

        checkEls['password'] = notEmpty;

        if ($('#passwordConfirmation').length) {
            checkEls['passwordConfirmation'] = notEmpty;
        }

        var configurable = function (name) {
            if (!$('#' + name).length) {
                return;
            }
            checkEls[name] = function (value) {
                if ($('#' + name)[0].hasAttribute('hidden')) {
                    return false;
                }
                if ($('#' + name)[0].hasAttribute('not-empty')) {
                    return notEmpty(value);
                }
                return false;
            };
        };

        configurable('fullName');
        configurable('readableName');
        configurable('data1');
        configurable('data2');
        configurable('data3');
        configurable('data4');
        configurable('gitHubUsername');
        configurable('slackEmail');

        var validateElements = function () {
            for (var index in checkEls) {
                if (!checkEls.hasOwnProperty(index)) {
                    continue;
                }

                var element = $('#' + index);
                var value = element.find('input').val();
                if (!element.is(':hidden') && checkEls[index](value)) {
                    element.addClass('not-valid');
                    element.removeClass('valid');
                } else {
                    element.addClass('valid');
                    element.removeClass('not-valid');
                }
            }
        };

        var validation = function (id) {
            var element = $('#' + id);

            element.keyup(validateElements);
            element.focus(validateElements);
            element.blur(validateElements);
            element.mousedown(validateElements);
            element.change(validateElements);

            validateElements();
        };

        $('#email').checkAndTriggerAutoFillEvent();
        $('#fullName').checkAndTriggerAutoFillEvent();
        $('#readableName').checkAndTriggerAutoFillEvent();
        $('#password').checkAndTriggerAutoFillEvent();
        $('#gitHubUsername').checkAndTriggerAutoFillEvent();
        $('#slackEmail').checkAndTriggerAutoFillEvent();

        for (var index in checkEls) {
            if (!checkEls.hasOwnProperty(index)) {
                continue;
            }

            validation(index);
        }

        var fixMd5 = function(element) {
            var from = element + ' input';
            var to = element + '-md5 input';
            if ($(from).length) {
                $(to).val($.md5($(from).val()));
            }
        }

        var submitForm = function () {
            if ($('form .not-valid').length == 0) {
                $('#data input').val(
                    $('#data1 input').val() + "|" +
                    $('#data2 input').val() + "|" +
                    $('#data3 input').val() + "|" +
                    $('#data4 input').val()
                );

                saveDataToLocalStorage();
                fixMd5('#password');
                fixMd5('#passwordConfirmation');
                $('#form').submit();
            }
        };

        $('#submit-button').click(submitForm);
        $('#email, #password, #skills, #fullName, #readableName, #data1, #data2, #data3, #data4, #gitHubUsername').keypress(function (e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                submitForm();
                e.preventDefault();
            }
        });
    }

    function loadInput(key, selector) {
        var value = localStorage.getItem(key);
        if (!!value && value !== 'undefined' && !$(selector).attr('hidden')) {
            $(selector).find('input').val(value);
        }
    }




    function fillFormFromLocalStorage(data) {
        loadInput(KEYS.userData.email, '#email');
        loadInput(KEYS.userData.fullName, '#fullName');
        loadInput(KEYS.userData.readableName, '#readableName');
        loadInput(KEYS.userData.data1, '#data1');
        loadInput(KEYS.userData.data2, '#data2');
        loadInput(KEYS.userData.data3, '#data3');
        loadInput(KEYS.userData.data4, '#data4');
        loadInput(KEYS.userData.github, '#gitHubUsername');
        loadInput(KEYS.userData.slackEmail, '#slackEmail');
    }

    function saveDataToLocalStorage() {
        localStorage.setItem(KEYS.userData.email, $('#email input').val());
        localStorage.setItem(KEYS.userData.fullName, $('#fullName input').val());
        localStorage.setItem(KEYS.userData.readableName, $('#readableName input').val());
        localStorage.setItem(KEYS.userData.data1, $('#data1 input').val());
        localStorage.setItem(KEYS.userData.data2, $('#data2 input').val());
        localStorage.setItem(KEYS.userData.data3, $('#data3 input').val());
        localStorage.setItem(KEYS.userData.data4, $('#data4 input').val());
        localStorage.setItem(KEYS.userData.github, $('#gitHubUsername input').val());
        localStorage.setItem(KEYS.userData.slackEmail, $('#slackEmail input').val());
    }

    $(document).ready(function() {
        validatePlayerRegistration("#player");
        if (waitApprove) {
            disable(true);
            $.ajax({ url:contextPath + '/register?approved=' + $("#name").val(),
                cache:false,
                complete:function(data) {
                    window.location.replace(data.responseText);
                },
                timeout:1000000
            });
        } else {
            if ($("#name").val() == "") {
                $("#name").focus();
            } else {
                $("#password").focus();
            }
            configureFormFromAdminSettings(loadRegistrationPage);
        }
    });
}
