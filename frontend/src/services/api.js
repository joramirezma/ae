// Use relative URL in production (Docker), absolute in development
const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

/**
 * Get stored JWT token from localStorage
 */
const getToken = () => localStorage.getItem('token');

/**
 * Set JWT token in localStorage
 */
export const setToken = (token) => localStorage.setItem('token', token);

/**
 * Remove JWT token from localStorage
 */
export const removeToken = () => localStorage.removeItem('token');

/**
 * Check if user is authenticated
 */
export const isAuthenticated = () => !!getToken();

/**
 * Get authorization headers
 */
const getAuthHeaders = () => {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

/**
 * Generic fetch wrapper with error handling
 */
const fetchApi = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const defaultHeaders = {
    'Content-Type': 'application/json',
    ...getAuthHeaders(),
  };

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
  };

  const response = await fetch(url, config);

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'An error occurred' }));
    throw new Error(error.message || `HTTP error! status: ${response.status}`);
  }

  // Handle empty responses
  const text = await response.text();
  return text ? JSON.parse(text) : null;
};

// ==================== AUTH API ====================

export const authApi = {
  /**
   * Register a new user
   */
  register: async (username, email, password) => {
    return fetchApi('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });
  },

  /**
   * Login user and get JWT token
   */
  login: async (username, password) => {
    const data = await fetchApi('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
    if (data.token) {
      setToken(data.token);
    }
    return data;
  },

  /**
   * Logout user
   */
  logout: () => {
    removeToken();
  },
};

// ==================== PROJECTS API ====================

export const projectsApi = {
  /**
   * Get all projects
   */
  getAll: async () => {
    return fetchApi('/projects');
  },

  /**
   * Get project by ID
   */
  getById: async (id) => {
    return fetchApi(`/projects/${id}`);
  },

  /**
   * Create a new project
   */
  create: async (name) => {
    return fetchApi('/projects', {
      method: 'POST',
      body: JSON.stringify({ name }),
    });
  },

  /**
   * Activate a project
   */
  activate: async (id) => {
    return fetchApi(`/projects/${id}/activate`, {
      method: 'PATCH',
    });
  },

  /**
   * Delete a project (soft delete)
   */
  delete: async (id) => {
    return fetchApi(`/projects/${id}`, {
      method: 'DELETE',
    });
  },
};

// ==================== TASKS API ====================

export const tasksApi = {
  /**
   * Get tasks by project ID
   */
  getByProject: async (projectId) => {
    return fetchApi(`/projects/${projectId}/tasks`);
  },

  /**
   * Create a new task
   */
  create: async (title, projectId) => {
    return fetchApi(`/projects/${projectId}/tasks`, {
      method: 'POST',
      body: JSON.stringify({ title }),
    });
  },

  /**
   * Complete a task
   */
  complete: async (id) => {
    return fetchApi(`/tasks/${id}/complete`, {
      method: 'PATCH',
    });
  },

  /**
   * Delete a task (soft delete)
   */
  delete: async (id) => {
    return fetchApi(`/tasks/${id}`, {
      method: 'DELETE',
    });
  },
};
