import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { projectsApi, tasksApi } from '../services/api';
import ProjectCard from '../components/ProjectCard';
import TaskCard from '../components/TaskCard';
import CreateProjectModal from '../components/CreateProjectModal';
import CreateTaskModal from '../components/CreateTaskModal';
import './Dashboard.css';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showProjectModal, setShowProjectModal] = useState(false);
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState('all');
  const [notification, setNotification] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [projectsData, tasksData] = await Promise.all([
        projectsApi.getAll(),
        tasksApi.getAll()
      ]);
      setProjects(projectsData || []);
      setTasks(tasksData || []);
    } catch (err) {
      setError(err.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => setNotification(''), 3000);
  };

  const handleCreateProject = async (name, description) => {
    try {
      const newProject = await projectsApi.create(name, description);
      setProjects([...projects, newProject]);
      setShowProjectModal(false);
      showNotification('Project created successfully!');
    } catch (err) {
      throw err;
    }
  };

  const handleActivateProject = async (id) => {
    try {
      const updatedProject = await projectsApi.activate(id);
      setProjects(projects.map(p => p.id === id ? updatedProject : p));
      showNotification('Project activated successfully!');
    } catch (err) {
      showNotification(`Error: ${err.message}`);
    }
  };

  const handleCreateTask = async (title, description, projectId) => {
    try {
      const newTask = await tasksApi.create(title, description, projectId);
      setTasks([...tasks, newTask]);
      setShowTaskModal(false);
      showNotification('Task created successfully!');
    } catch (err) {
      throw err;
    }
  };

  const handleCompleteTask = async (id) => {
    try {
      const updatedTask = await tasksApi.complete(id);
      setTasks(tasks.map(t => t.id === id ? updatedTask : t));
      showNotification('Task completed!');
    } catch (err) {
      showNotification(`Error: ${err.message}`);
    }
  };

  const filteredTasks = selectedProjectId === 'all' 
    ? tasks 
    : tasks.filter(t => t.projectId === selectedProjectId);

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner"></div>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      {/* Header */}
      <header className="dashboard-header">
        <h1>🚀 Project & Task Manager</h1>
        <div className="header-right">
          <span className="user-name">👤 {user?.username}</span>
          <button onClick={logout} className="btn-logout">Logout</button>
        </div>
      </header>

      {/* Notification */}
      {notification && (
        <div className="notification">{notification}</div>
      )}

      {/* Error */}
      {error && (
        <div className="error-banner">{error}</div>
      )}

      {/* Main Content */}
      <main className="dashboard-content">
        {/* Projects Section */}
        <section className="section">
          <div className="section-header">
            <h2>📁 Projects</h2>
            <button onClick={() => setShowProjectModal(true)} className="btn-add">
              + New Project
            </button>
          </div>
          <div className="cards-grid">
            {projects.length === 0 ? (
              <p className="empty-message">No projects yet. Create your first project!</p>
            ) : (
              projects.map(project => (
                <ProjectCard
                  key={project.id}
                  project={project}
                  onActivate={handleActivateProject}
                  taskCount={tasks.filter(t => t.projectId === project.id).length}
                />
              ))
            )}
          </div>
        </section>

        {/* Tasks Section */}
        <section className="section">
          <div className="section-header">
            <h2>✅ Tasks</h2>
            <div className="section-actions">
              <select 
                value={selectedProjectId} 
                onChange={(e) => setSelectedProjectId(e.target.value)}
                className="filter-select"
              >
                <option value="all">All Projects</option>
                {projects.map(p => (
                  <option key={p.id} value={p.id}>{p.name}</option>
                ))}
              </select>
              <button 
                onClick={() => setShowTaskModal(true)} 
                className="btn-add"
                disabled={projects.filter(p => p.status === 'ACTIVE').length === 0}
              >
                + New Task
              </button>
            </div>
          </div>
          <div className="tasks-list">
            {filteredTasks.length === 0 ? (
              <p className="empty-message">No tasks yet. Create your first task!</p>
            ) : (
              filteredTasks.map(task => (
                <TaskCard
                  key={task.id}
                  task={task}
                  projectName={projects.find(p => p.id === task.projectId)?.name || 'Unknown'}
                  onComplete={handleCompleteTask}
                />
              ))
            )}
          </div>
        </section>
      </main>

      {/* Modals */}
      {showProjectModal && (
        <CreateProjectModal
          onClose={() => setShowProjectModal(false)}
          onCreate={handleCreateProject}
        />
      )}

      {showTaskModal && (
        <CreateTaskModal
          onClose={() => setShowTaskModal(false)}
          onCreate={handleCreateTask}
          projects={projects.filter(p => p.status === 'ACTIVE')}
        />
      )}
    </div>
  );
};

export default Dashboard;
