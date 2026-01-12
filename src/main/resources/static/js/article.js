
function validateAndGetFormData() {
    const titleInput = document.getElementById('title');
    const contentInput = document.getElementById('content');
    const fileInput = document.getElementById('file-input');
    const isNoticeCheckbox = document.getElementById('is-notice');

    const titleValue = titleInput.value;
    const contentValue = contentInput.value;

    // 1. 제목 검사
    if (!titleValue || titleValue.trim() === "") {
        alert("제목을 입력해주세요.");
        titleInput.focus();
        return null;
    }

    // 2. 본문 검사
    if (!contentValue || contentValue.trim() === "") {
        alert("본문 내용을 입력해주세요.");
        contentInput.focus();
        return null;
    }

    let formData = new FormData();
    formData.append('title', titleValue);
    formData.append('content', contentValue);

    const isNotice = isNoticeCheckbox ? isNoticeCheckbox.checked : false;
    formData.append('isNotice', isNotice);

    // 파일이 선택되었을 때만 추가
    if (fileInput && fileInput.files[0]) {
        formData.append('image', fileInput.files[0]);
    }

    return formData;
}


// 삭제
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;

        function success() {
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }

        function fail() {
            alert('삭제 실패했습니다.');
            location.replace('/articles');
        }

        httpRequest('DELETE', `/api/articles/${id}`, null, success, fail);
    });
}


// 수정
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {

        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        const formData = validateAndGetFormData();
        if (!formData) return;

        function success() {
            alert('수정 완료되었습니다.');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('수정 실패했습니다.');
            location.replace(`/articles/${id}`);
        }

        httpRequest('PUT', `/api/articles/${id}`, formData, success, fail);
    });
}


// 생성
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {

        const formData = validateAndGetFormData();
        if (!formData) return;

        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/articles');
        }

        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/articles');
        }

        httpRequest('POST', '/api/articles', formData, success, fail);
    });
}

// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');
        var dic = item.split('=');
        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });
    return result;
}

// HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    // 헤더 설정
    const headers = {};

    const accessToken = localStorage.getItem('access_token');
    if (accessToken) {
        headers['Authorization'] = 'Bearer ' + accessToken;
    }
    // FormData가 아닐 때만 Content-Type을 application/json으로 설정
    if (!(body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    fetch(url, {
        method: method,
        headers: headers,
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then(result => {
                    // 재발급 성공 시 토큰 교체 후 재요청
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    });
}