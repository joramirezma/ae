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
  const [tasksByProject, setTasksByProject] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showProjectModal, setShowProjectModal] = useState(false);
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [notification, setNotification] = useState('');

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    setLoading(true);
    setError('');
    try {
      const projectsData = await projectsApi.getAll();
      setProjects(projectsData || []);
      
      // Load tasks for each project
      const tasksMap = {};
      for (const project of (projectsData || [])) {
        try {
          const projectTasks = await tasksApi.getByProject(project.id);
          tasksMap[project.id] = projectTasks || [];
        } catch {
          tasksMap[project.id] = [];
        }
      }
      setTasksByProject(tasksMap);
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

  const handleCreateProject = async (name) => {
    try {
      const newProject = await projectsApi.create(name);
      setProjects([...projects, newProject]);
      setTasksByProject(prev => ({ ...prev, [newProject.id]: [] }));
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

  const handleDeleteProject = async (id) => {
    if (!confirm('Are you sure you want to delete this project? All tasks will be deleted too.')) {
      return;
    }
    try {
      await projectsApi.delete(id);
      setProjects(projects.filter(p => p.id !== id));
      setTasksByProject(prev => {
        const newState = { ...prev };
        delete newState[id];
        return newState;
      });
      showNotification('Project deleted successfully!');
    } catch (err) {
      showNotification(`Error: ${err.message}`);
    }
  };

  const handleCreateTask = async (title, projectId) => {
    try {
      const newTask = await tasksApi.create(title, projectId);
      setTasksByProject(prev => ({
        ...prev,
        [projectId]: [...(prev[projectId] || []), newTask]
      }));
      setShowTaskModal(false);
      showNotification('Task created successfully!');
    } catch (err) {
      throw err;
    }
  };

  const handleCompleteTask = async (id, projectId) => {
    try {
      const updatedTask = await tasksApi.complete(id);
      setTasksByProject(prev => ({
        ...prev,
        [projectId]: (prev[projectId] || []).map(t => t.id === id ? updatedTask : t)
      }));
      showNotification('Task completed!');
    } catch (err) {
      showNotification(`Error: ${err.message}`);
    }
  };

  const handleDeleteTask = async (id, projectId) => {
    if (!confirm('Are you sure you want to delete this task?')) {
      return;
    }
    try {
      await tasksApi.delete(id);
      setTasksByProject(prev => ({
        ...prev,
        [projectId]: (prev[projectId] || []).filter(t => t.id !== id)
      }));
      showNotification('Task deleted successfully!');
    } catch (err) {
      showNotification(`Error: ${err.message}`);
    }
  };

  const getTasksForProject = (projectId) => {
    return tasksByProject[projectId] || [];
  };

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
          
          {projects.length === 0 ? (
            <p className="empty-message">No projects yet. Create your first project!</p>
          ) : (
            <div className="projects-list">
              {projects.map(project => (
                <div key={project.id} className="project-container">
                  <ProjectCard
                    project={project}
                    onActivate={handleActivateProject}
                    taskCount={getTasksForProject(project.id).length}
                    onAddTask={() => {
                      setSelectedProjectId(project.id);
                      setShowTaskModal(true);
                    }}
                    onDelete={handleDeleteProject}
                  />
                  
                  {/* Tasks for this project */}
                  <div className="project-tasks">
                    {getTasksForProject(project.id).length === 0 ? (
                      <p className="empty-tasks">No tasks in this project</p>
                    ) : (
                      getTasksForProject(project.id).map(task => (
                        <TaskCard
                          key={task.id}
                          task={task}
                          projectStatus={project.status}
                          onComplete={() => handleCompleteTask(task.id, project.id)}
                          onDelete={() => handleDeleteTask(task.id, project.id)}
                        />
                      ))
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
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
          onClose={() => {
            setShowTaskModal(false);
            setSelectedProjectId(null);
          }}
          onCreate={handleCreateTask}
          projects={selectedProjectId ? [projects.find(p => p.id === selectedProjectId)] : projects}
          preselectedProjectId={selectedProjectId}
        />
      )}
    </div>
  );
};

export default Dashboard;
