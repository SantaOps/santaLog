document.addEventListener("DOMContentLoaded", function () {
    console.log("Article JS 로드 완료 및 DOM 탐색 시작");

    // --- 1. 유효성 검사 및 데이터 수집 ---
    function validateAndGetFormData() {
        const titleInput = document.getElementById('title');
        const contentInput = document.getElementById('content');
        const fileInput = document.getElementById('file-input');
        const isNoticeCheckbox = document.getElementById('is-notice');

        if (!titleInput || !contentInput) {
            console.error("필수 입력 필드(title 또는 content)를 찾을 수 없습니다.");
            return null;
        }

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

        // 'image'라는 필드명으로 파일 추가 (백엔드 MultipartFile 필드명과 일치해야 함)
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
            const articleIdElement = document.getElementById('article-id');
            if (!articleIdElement) {
                alert("수정할 게시글 ID를 찾을 수 없습니다.");
                return;
            }
            const id = articleIdElement.value;
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
            const articleIdElement = document.getElementById('article-id');
            if (!articleIdElement) return;
            const id = articleIdElement.value;

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

    /* 중요: FormData를 보낼 때는 Content-Type 헤더를 수동으로 설정하면 안 됩니다.
       브라우저가 자동으로 'multipart/form-data; boundary=...'를 설정해야 하기 때문입니다.
    */
    if (!(body instanceof FormData) && body !== null) {
        headers['Content-Type'] = 'application/json';
    }

    console.log(`${method} 요청 전송: ${url}`);

    fetch(url, {
        method: method,
        headers: headers,
        body: body, // FormData인 경우 그대로 전송
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }

        // 토큰 재발급 로직 (401 에러 발생 시)
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
                // 새 토큰으로 원래 요청 재시도
                httpRequest(method, url, body, success, fail);
            })
            .catch(() => {
                console.error("토큰 재발급 실패");
                fail();
            });
        } else {
            console.error("요청 실패 상태 코드:", response.status);
            return fail();
        }
    }).catch(error => {
        console.error("Fetch 네트워크 에러:", error);
        fail();
    });
}