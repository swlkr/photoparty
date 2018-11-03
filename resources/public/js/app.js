$(function() {
  $(document).on("click", "[data-confirm]", function(e) {
    e.preventDefault();
    var el = e.target;
    var $this = $(this);

    swal({
      title: el.getAttribute("data-title") || "Are you sure?",
      text: el.getAttribute("data-text") || "If you click Yes, it will be deleted.",
      icon: el.getAttribute("data-icon") || "warning",
      buttons: {
        cancel: el.getAttribute("data-cancel") || "No",
        confirm: el.getAttribute("data-confirm-button") || "Yes",
      }
    })
    .then(function(confirm) {
      if (confirm) {
        $this.submit();
      }
    });
  })

  $("time").each(function() {
    var $this = $(this)
    var s = $this.text();
    var d = new Date(s);

    if($this.data("date")) {
      $this.text(d.toLocaleDateString())
    } else {
      $this.text(d.toLocaleDateString() + " " + d.toLocaleTimeString())
    }
  })
})

Dropzone.options.dropzone = {
  autoProcessQueue: false,
  maxFilesize: 100, // MB
  acceptedFiles: "image/*",
  maxThumbnailFilesize: 50, // MB
  thumbnailWidth: 150,
  thumbnailHeight: 150,
  addRemoveLinks: true,
  dictRemoveFile: "Remove",
  dictCancelUpload: "Cancel",
  uploadMultiple: true,
  parallelUploads: 100,
  successmultiple: function(files, response) {
    window.location.href = "/album/" + response["album-ident"]
  },
  init: function() {
    var submitButton = document.querySelector("#submit-all")
        myDropzone = this; // closure

    submitButton.addEventListener("click", function(e) {
      e.preventDefault();

      myDropzone.processQueue(); // Tell Dropzone to process all queued files.
    });

    // You might want to show the submit button only when
    // files are dropped here:
    this.on("addedfile", function() {
      $(submitButton).addClass("db").removeClass("dn")
      // Show submit button here and/or inform user to click it.
    });
  }
};
