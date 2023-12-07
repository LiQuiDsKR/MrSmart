var abcHttp = (function() {
	function post(url, paramData, redirection) {
		return new Promise((resolve, reject) => {
			console.log(document.querySelector('meta[name="_csrf"]').content);
			fetch(url, {
				method: "POST",
				headers: {
					'header': document.querySelector('meta[name="_csrf_header"]').content,
		            "Content-Type": "application/json",
		            'X-CSRF-Token': document.querySelector('meta[name="_csrf"]').content
				},
				body: JSON.stringify(paramData),
			})
			.then(response => {
				if (!response.ok && response!=null) {
					return response.text().then(textData=>{
						throw new Error(`${response.status} / ${textData}`);
					});
				}
				return response.json();
			})
			.then(data => {
				if (redirection != null) {
					location.href = redirection;
				} else {
					resolve(data);
				}
			})
			.catch(error => {
				console.error('Error:', error);
				Swal.fire({
					type: "error",
					title: "Error!",
					text: error,
				});
				setTimeout(() => {
					$("#contactAddModal").modal("hide");
					$("#contactEditModal").modal("hide");
				}, 0);
				reject(error);
			});
		});
	}
	function get(url, paramData, redirection) {
		return new Promise((resolve, reject) => {
			var resultUrl = url;
			if (paramData!=null && Object.keys(paramData).length>0) {
				const params = new URLSearchParams();
				for (const key in paramData) {
					params.append(key, paramData[key]);
				}
				resultUrl = url + "?" + params.toString();
			}
			fetch(resultUrl,{
				method: "GET",
				headers: {
					//'header': document.querySelector('meta[name="_csrf"]').content,
		            "Content-Type": "application/json",
		            //'X-CSRF-Token': document.querySelector('meta[name="_csrf_header"]').content
				}
			})
			.then(response => {
				if (!response.ok && response!=null) {
					return response.text().then(textData=>{
						throw new Error(`${response.status} / ${textData}`);
					});
				}
				return response.json().catch(()=>({}));
			})
			.then(data => {
				if (redirection != null) {
					location.href = redirection;
				} else {
					resolve(data);
				}
			})
			.catch(error => {
				console.error('Error:', error);
				Swal.fire({
					type: "error",
					title: "Error!",
					text: error,
				});
				setTimeout(() => {
					$("#contactAddModal").modal("hide");
					$("#contactEditModal").modal("hide");
				}, 0);
				reject(error);
			});
		});
	}
	
	function multiplyFontSize(multiplier){
		// 모든 <link> 태그 가져오기
		var linkTags = document.querySelectorAll('link[rel="stylesheet"]');
		// 추가된 <style> 태그의 식별자
		var injectedStyleIdentifier = 'data-injected';
				
		// 추가된 <style> 태그 제거
	    var injectedStyleTags = document.querySelectorAll('style[' + injectedStyleIdentifier + ']');
	    injectedStyleTags.forEach(function(styleTag) {
	        styleTag.remove();
	    });
	    
	    if (multiplier==1){ return ;}

		// 각 CSS 파일에 대해 실행
		linkTags.forEach(function(linkTag) {
		    // CSS 파일의 경로 가져오기
		    var cssFilePath = linkTag.getAttribute('href');
		
		    // CSS 파일을 비동기적으로 가져와 처리
		    fetch(cssFilePath)
		        .then(response => response.text())
		        .then(cssText => {
                
		            // font-size를 두 배로 변경
		            var modifiedCssText = cssText.replace(/font-size\s*:\s*([^;]+);/g, function(match, p1) {
						 // !important가 있는지 확인
	                	var important = p1.includes('!important') ? ' !important' : '';
	                	
		                var newSize = parseFloat(p1) * multiplier + 'px';
		                return 'font-size: ' + newSize + important + ';';
		            });
		
		            // 변경된 CSS를 <style> 태그로 추가
		            var styleTag = document.createElement('style');
					styleTag.setAttribute(injectedStyleIdentifier, ''); // 추가된 태그를 식별하기 위한 데이터 속성
		            styleTag.innerHTML = modifiedCssText;
		            document.head.appendChild(styleTag);
		        })
		        .catch(error => console.error('CSS 파일을 불러오는 중 오류 발생:', error));
		});
	}
	
	
	return {
		get:get,
		post:post,
		multiplyFontSize:multiplyFontSize,
	}
})();