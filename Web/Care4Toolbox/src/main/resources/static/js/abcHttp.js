var abcHttp = (function() {
	function post(url, paramData, redirection) {
		return new Promise((resolve, reject) => {
			fetch(url, {
				method: "POST",
				headers: {
					//'header': document.querySelector('meta[name="_csrf"]').content,
		            "Content-Type": "application/json",
		            //'X-CSRF-Token': document.querySelector('meta[name="_csrf_header"]').content
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
	
	return {
		get:get,
		post:post,
	}
})();