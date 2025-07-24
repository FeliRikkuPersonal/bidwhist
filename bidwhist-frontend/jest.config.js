// jest.config.js
export default {
  testEnvironment: 'jsdom',
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy'
  },
  transform: {
    '^.+\\.[jt]sx?$': 'babel-jest'
  },
  setupFilesAfterEnv: ['<rootDir>/setupTests.js'],
};
