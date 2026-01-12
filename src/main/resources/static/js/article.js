// 삭제 기능
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

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        // [수정] JSON 대신 FormData 사용
        let formData = new FormData();
        formData.append('title', document.getElementById('title').value);
        formData.append('content', document.getElementById('content').value);

        // 파일이 선택되었을 때만 추가
        const fileInput = document.getElementById('file-input');
        if (fileInput && fileInput.files[0]) {
            // 백엔드 Controller에서 받는 파라미터 이름(예: "image")과 일치해야 함
            formData.append('image', fileInput.files[0]);
        }

        function success() {
            alert('수정 완료되었습니다.');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('수정 실패했습니다.');
            location.replace(`/articles/${id}`);
        }

        // FormData 객체를 그대로 넘김 (JSON.stringify 안함)
        httpRequest('PUT', `/api/articles/${id}`, formData, success, fail);
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        // [수정] JSON 대신 FormData 사용
        let formData = new FormData();
        formData.append('title', document.getElementById('title').value);
        formData.append('content', document.getElementById('content').value);

        // 파일 추가
        const fileInput = document.getElementById('file-input');
        if (fileInput && fileInput.files[0]) {
            // 백엔드 Controller에서 받는 파라미터 이름(예: "image")과 일치해야 함
            formData.append('image', fileInput.files[0]);
        }

        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/articles');
        };
        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/articles');
        };

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

// HTTP 요청을 보내는 함수 (핵심 수정 부분)
function httpRequest(method, url, body, success, fail) {
    // 헤더 설정
    const headers = {};

    const accessToken = localStorage.getItem('access_token');
    if (accessToken) {
        headers['Authorization'] = 'Bearer ' + accessToken;
    }
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