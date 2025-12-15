# Use Node LTS (works well with Angular 17+)
FROM node:20-alpine

# Set working directory inside container
WORKDIR /app

# Copy package files first (better caching)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the project
COPY . .

# Expose Angular dev server port
EXPOSE 4200

# Run Angular dev server
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0"]
