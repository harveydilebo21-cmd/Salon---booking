document.addEventListener('DOMContentLoaded', () => {

  // 1. Ask before risky actions: <form data-confirm="Are you sure?">
  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
      if (!confirm(form.dataset.confirm)) {
        e.preventDefault();
      }
    });
  });

  // 2. Stop double submits (runs after the confirm check above)
  document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', e => {
      if (e.defaultPrevented) return;
      const button = form.querySelector('button[type="submit"]');
      if (button) {
        button.disabled = true;
        button.textContent = 'Please wait...';
      }
    });
  });

  // 3. Stop people picking a time in the past
  const now = new Date();
  const pad = n => String(n).padStart(2, '0');
  const local = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`
              + `T${pad(now.getHours())}:${pad(now.getMinutes())}`;
  document.querySelectorAll('input[type="datetime-local"]').forEach(input => input.min = local);

  // 4. Fade out success messages after 5 seconds
  document.querySelectorAll('.alert.ok').forEach(el => {
    setTimeout(() => el.classList.add('hide'), 5000);
  });
});

// If the user presses Back, reload so buttons are not stuck on "Please wait..."
window.addEventListener('pageshow', e => {
  if (e.persisted) location.reload();
});