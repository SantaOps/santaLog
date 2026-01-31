document.addEventListener("DOMContentLoaded", function () {
    console.log("Article JS 로드 완료 및 DOM 탐색 시작");

    // --- 1. 유효성 검사 및 데이터 수집 ---
    function validateAndGetFormData() {
        const titleInput = document.getElementById('title');
        const contentInput = document.getElementById('content');
        const fileInput = document.getElementById('file-input');
        const isNoticeCheckbox = document.getElementById('is-notice');

        if (!titleInput || !contentInput) return null;

        const titleValue = titleInput.value.trim();
        const contentValue = contentInput.value.trim();

        if (!titleValue) {
            alert("제목을 입력해주세요.");
            titleInput.focus();
            return null;
        }

        if (!contentValue) {
            alert("본문 내용을 입력해주세요.");
            contentInput.focus();
            return null;
        }

        let formData = new FormData();
        formData.append('title', titleValue);
        formData.append('content', contentValue);

        // 관리자가 아닐 경우 체크박스가 없을 수 있으므로 방어 코드 작성
        const isNotice = isNoticeCheckbox ? isNoticeCheckbox.checked : false;
        formData.append('isNotice', isNotice);

        if (fileInput && fileInput.files[0]) {
            formData.append('image', fileInput.files[0]);
        }

        return formData;
    }

    // --- 2. 생성 버튼 이벤트 ---
    const createButton = document.getElementById('create-btn');
    if (createButton) {
        createButton.addEventListener('click', () => {
            console.log("등록 버튼 클릭됨");
            const formData = validateAndGetFormData();
            if (!formData) return;

            httpRequest('POST', '/api/articles', formData,
                () => { alert('등록 완료되었습니다.'); location.replace('/articles'); },
                () => { alert('등록 실패했습니다.'); }
            );
        });
    }

    // --- 3. 수정 버튼 이벤트 ---
    const modifyButton = document.getElementById('modify-btn');
    if (modifyButton) {
        modifyButton.addEventListener('click', () => {
            const id = document.getElementById('article-id').value;
            const formData = validateAndGetFormData();
            if (!formData) return;

            httpRequest('PUT', `/api/articles/${id}`, formData,
                () => { alert('수정 완료되었습니다.'); location.replace(`/articles/${id}`); },
                () => { alert('수정 실패했습니다.'); }
            );
        });
    }

    // --- 4. 삭제 버튼 이벤트 ---
    const deleteButton = document.getElementById('delete-btn');
    if (deleteButton) {
        deleteButton.addEventListener('click', () => {
            const id = document.getElementById('article-id').value;
            if (confirm("정말 삭제하시겠습니까?")) {
                httpRequest('DELETE', `/api/articles/${id}`, null,
                    () => { alert('삭제 완료되었습니다.'); location.replace('/articles'); },
                    () => { alert('삭제 실패했습니다.'); }
                );
            }
        });
    }
});

// --- 5. 공통 HTTP 요청 함수 (전역 유지) ---
function getCookie(key) {
    let result = null;
    let cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');
        let dic = item.split('=');
        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });
    return result;
}

function httpRequest(method, url, body, success, fail) {
    const headers = {};
    const accessToken = localStorage.getItem('access_token');

    if (accessToken) {
        headers['Authorization'] = 'Bearer ' + accessToken;
    }

    // FormData가 아닐 때만 Content-Type 설정 (Multipart는 브라우저가 자동 설정해야 함)
    if (!(body instanceof FormData) && body !== null) {
        headers['Content-Type'] = 'application/json';
    }

    console.log(`${method} 요청 전송: ${url}`);

    fetch(url, {
        method: method,
        headers: headers,
        body: body,
    }).then(response => {
        if (response.ok) {
            return success();
        }

        // 토큰 재발급 로직
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: refresh_token }),
            })
            .then(res => {
                if (res.ok) return res.json();
                throw new Error();
            })
            .then(result => {
                localStorage.setItem('access_token', result.accessToken);
                httpRequest(method, url, body, success, fail); // 재시도
            })
            .catch(() => fail());
        } else {
            return fail();
        }
    }).catch(error => {
        console.error("Fetch 에러:", error);
        fail();
    });
}