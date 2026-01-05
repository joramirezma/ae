import { useState } from 'react';

const CreateTaskModal = ({ onClose, onCreate, projects, preselectedProjectId }) => {
  const [title, setTitle] = useState('');
  const [projectId, setProjectId] = useState(preselectedProjectId || projects[0]?.id || '');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Find the selected project to display its name
  const selectedProject = projects.find(p => p.id === projectId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setError('Task title is required');
      return;
    }
    if (!projectId) {
      setError('Please select a project');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await onCreate(title, projectId);
    } catch (err) {
      setError(err.message || 'Failed to create task');
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>✅ Create New Task</h3>
        {selectedProject && (
          <p className="project-context">
            Adding task to: <strong>{selectedProject.name}</strong>
          </p>
        )}
        <form onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}
          <div className="form-group">
            <label htmlFor="task-title">Task Title *</label>
            <input
              type="text"
              id="task-title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Enter task title"
              autoFocus
            />
          </div>
          {!preselectedProjectId && projects.length > 1 && (
            <div className="form-group">
              <label htmlFor="task-project">Project *</label>
              <select
                id="task-project"
                value={projectId}
                onChange={(e) => setProjectId(e.target.value)}
              >
                {projects.map((project) => (
                  <option key={project.id} value={project.id}>
                    {project.name}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="modal-actions">
            <button type="button" onClick={onClose} className="btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Creating...' : 'Create Task'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateTaskModal;
