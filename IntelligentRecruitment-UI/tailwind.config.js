/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      fontFamily: {
        archivo: ['Archivo', 'sans-serif'],
        manrope: ['Manrope', 'sans-serif'],
      },
      colors: {
        'teal-brand':  '#0f766e',
        'teal-deep':   '#0d5e57',
        'teal-light':  '#14b8a6',
        'teal-xlight': '#ccfbf1',
        charcoal:      '#0f172a',
        warm:          '#f8faf9',
      },
    },
  },
  plugins: [],
};
